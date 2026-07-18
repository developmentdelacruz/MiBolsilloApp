package com.delacruz.mibolsilloapp.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migraciones reales, obtenidas diffeando los JSON de esquema exportados en
 * app/schemas/com.delacruz.mibolsilloapp.data.local.AppDatabase/{N}.json contra la versión
 * siguiente. Reemplazan fallbackToDestructiveMigration: de acá en más, todo bump de versión
 * de AppDatabase debe venir acompañado de su Migration — si falta alguna, Room lanza una
 * excepción en el próximo arranque en vez de borrar la base silenciosamente.
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `cuentas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`nombre` TEXT NOT NULL, `tipo` TEXT NOT NULL, `monedaId` INTEGER NOT NULL, " +
                "`saldoInicialCentavos` INTEGER NOT NULL, `activa` INTEGER NOT NULL, " +
                "FOREIGN KEY(`monedaId`) REFERENCES `monedas`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )",
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_cuentas_monedaId` ON `cuentas` (`monedaId`)")

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `presupuestos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`categoriaId` INTEGER NOT NULL, `montoMensualCentavos` INTEGER NOT NULL, " +
                "FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_presupuestos_categoriaId` ON `presupuestos` (`categoriaId`)",
        )

        // v1 no tenía cuentaId en transacciones. Si ya había filas cargadas, no hay forma de
        // saber de qué cuenta salió cada una — se les asigna una única cuenta placeholder
        // "Cuenta migrada" (creando antes una moneda por defecto si no existía ninguna, porque
        // cuentas.monedaId es NOT NULL) para no perder las filas ni violar el NOT NULL nuevo.
        // Si no había transacciones todavía (instalación nueva), ninguna de las dos filas se
        // inserta y la tabla queda igual de vacía que antes.
        db.execSQL(
            "INSERT INTO monedas (codigo, nombre, simbolo, esPredeterminada) " +
                "SELECT 'USD', 'Dólar estadounidense', '$', 1 " +
                "WHERE NOT EXISTS (SELECT 1 FROM monedas) AND EXISTS (SELECT 1 FROM transacciones)",
        )
        db.execSQL(
            "INSERT INTO cuentas (nombre, tipo, monedaId, saldoInicialCentavos, activa) " +
                "SELECT 'Cuenta migrada', 'EFECTIVO', " +
                "(SELECT id FROM monedas ORDER BY esPredeterminada DESC, id ASC LIMIT 1), 0, 1 " +
                "WHERE EXISTS (SELECT 1 FROM transacciones)",
        )

        // SQLite no permite agregar una FOREIGN KEY con ALTER TABLE ADD COLUMN, así que hay que
        // reconstruir la tabla completa para meter cuentaId con su referencia a `cuentas`.
        db.execSQL(
            "CREATE TABLE `transacciones_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`descripcion` TEXT NOT NULL, `montoCentavos` INTEGER NOT NULL, `fecha` TEXT NOT NULL, " +
                "`tipo` TEXT NOT NULL, `categoriaId` INTEGER NOT NULL, `cuentaId` INTEGER NOT NULL, " +
                "`negocioId` INTEGER, `proyectoId` INTEGER, " +
                "FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , " +
                "FOREIGN KEY(`negocioId`) REFERENCES `negocios`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , " +
                "FOREIGN KEY(`proyectoId`) REFERENCES `proyectos`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , " +
                "FOREIGN KEY(`cuentaId`) REFERENCES `cuentas`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )",
        )
        db.execSQL(
            "INSERT INTO transacciones_new " +
                "(id, descripcion, montoCentavos, fecha, tipo, categoriaId, cuentaId, negocioId, proyectoId) " +
                "SELECT id, descripcion, montoCentavos, fecha, tipo, categoriaId, " +
                "(SELECT id FROM cuentas ORDER BY id ASC LIMIT 1), negocioId, proyectoId FROM transacciones",
        )
        db.execSQL("DROP TABLE transacciones")
        db.execSQL("ALTER TABLE transacciones_new RENAME TO transacciones")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_categoriaId` ON `transacciones` (`categoriaId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_negocioId` ON `transacciones` (`negocioId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_proyectoId` ON `transacciones` (`proyectoId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_cuentaId` ON `transacciones` (`cuentaId`)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `compras` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`descripcion` TEXT NOT NULL, `montoTotalCentavos` INTEGER NOT NULL, " +
                "`cuotasTotales` INTEGER NOT NULL, `categoriaId` INTEGER NOT NULL, `cuentaId` INTEGER NOT NULL, " +
                "`negocioId` INTEGER, `fechaPrimeraCuota` TEXT NOT NULL, " +
                "FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , " +
                "FOREIGN KEY(`cuentaId`) REFERENCES `cuentas`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , " +
                "FOREIGN KEY(`negocioId`) REFERENCES `negocios`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )",
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_compras_categoriaId` ON `compras` (`categoriaId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_compras_cuentaId` ON `compras` (`cuentaId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_compras_negocioId` ON `compras` (`negocioId`)")

        db.execSQL("ALTER TABLE cuentas ADD COLUMN limiteCreditoCentavos INTEGER")

        // No se usa ALTER TABLE ADD COLUMN ... DEFAULT para creadoEn a propósito: SQLite deja el
        // DEFAULT grabado permanentemente en el esquema de la tabla, pero el creadoEn que Room
        // espera (según AppDatabase/3.json) es `TEXT NOT NULL` SIN default — un ALTER con DEFAULT
        // dejaría el esquema real desalineado del esperado y Room tira "Migration didn't
        // properly handle" al validar. Se reconstruye la tabla para que quede exacta.
        db.execSQL(
            "CREATE TABLE `presupuestos_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`categoriaId` INTEGER NOT NULL, `montoMensualCentavos` INTEGER NOT NULL, " +
                "`creadoEn` TEXT NOT NULL, " +
                "FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        db.execSQL(
            "INSERT INTO presupuestos_new (id, categoriaId, montoMensualCentavos, creadoEn) " +
                "SELECT id, categoriaId, montoMensualCentavos, date('now') FROM presupuestos",
        )
        db.execSQL("DROP TABLE presupuestos")
        db.execSQL("ALTER TABLE presupuestos_new RENAME TO presupuestos")
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_presupuestos_categoriaId` ON `presupuestos` (`categoriaId`)",
        )

        // transacciones gana compraId/numeroCuota (nullable) + FK nueva a compras -> requiere
        // reconstruir la tabla, SQLite no permite agregar FOREIGN KEY con ALTER TABLE.
        db.execSQL(
            "CREATE TABLE `transacciones_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`descripcion` TEXT NOT NULL, `montoCentavos` INTEGER NOT NULL, `fecha` TEXT NOT NULL, " +
                "`tipo` TEXT NOT NULL, `categoriaId` INTEGER NOT NULL, `cuentaId` INTEGER NOT NULL, " +
                "`negocioId` INTEGER, `proyectoId` INTEGER, `compraId` INTEGER, `numeroCuota` INTEGER, " +
                "FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , " +
                "FOREIGN KEY(`negocioId`) REFERENCES `negocios`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , " +
                "FOREIGN KEY(`proyectoId`) REFERENCES `proyectos`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , " +
                "FOREIGN KEY(`cuentaId`) REFERENCES `cuentas`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , " +
                "FOREIGN KEY(`compraId`) REFERENCES `compras`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )",
        )
        db.execSQL(
            "INSERT INTO transacciones_new (id, descripcion, montoCentavos, fecha, tipo, categoriaId, " +
                "cuentaId, negocioId, proyectoId, compraId, numeroCuota) " +
                "SELECT id, descripcion, montoCentavos, fecha, tipo, categoriaId, cuentaId, negocioId, " +
                "proyectoId, NULL, NULL FROM transacciones",
        )
        db.execSQL("DROP TABLE transacciones")
        db.execSQL("ALTER TABLE transacciones_new RENAME TO transacciones")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_categoriaId` ON `transacciones` (`categoriaId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_negocioId` ON `transacciones` (`negocioId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_proyectoId` ON `transacciones` (`proyectoId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_cuentaId` ON `transacciones` (`cuentaId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transacciones_compraId` ON `transacciones` (`compraId`)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `patrimonio_snapshots` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`fecha` TEXT NOT NULL, `valorCentavos` INTEGER NOT NULL)",
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_patrimonio_snapshots_fecha` " +
                "ON `patrimonio_snapshots` (`fecha`)",
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Mismo motivo que creadoEn en MIGRATION_2_3: un ALTER TABLE ... ADD COLUMN ... DEFAULT 1
        // deja el DEFAULT grabado en el esquema real, pero AppDatabase/5.json espera `activo`
        // como `INTEGER NOT NULL` sin default — hay que reconstruir la tabla para que coincidan
        // byte a byte y Room no tire "Migration didn't properly handle" al validar.
        db.execSQL(
            "CREATE TABLE `presupuestos_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`categoriaId` INTEGER NOT NULL, `montoMensualCentavos` INTEGER NOT NULL, " +
                "`creadoEn` TEXT NOT NULL, `activo` INTEGER NOT NULL, " +
                "FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        db.execSQL(
            "INSERT INTO presupuestos_new (id, categoriaId, montoMensualCentavos, creadoEn, activo) " +
                "SELECT id, categoriaId, montoMensualCentavos, creadoEn, 1 FROM presupuestos",
        )
        db.execSQL("DROP TABLE presupuestos")
        db.execSQL("ALTER TABLE presupuestos_new RENAME TO presupuestos")
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_presupuestos_categoriaId` ON `presupuestos` (`categoriaId`)",
        )
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `gastos_compartidos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`transaccionId` INTEGER NOT NULL, `nombreContacto` TEXT NOT NULL, `telefono` TEXT NOT NULL, " +
                "`montoAPagarCentavos` INTEGER NOT NULL, `estadoPago` TEXT NOT NULL, " +
                "FOREIGN KEY(`transaccionId`) REFERENCES `transacciones`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_gastos_compartidos_transaccionId` ON `gastos_compartidos` (`transaccionId`)",
        )
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `alertas_presupuesto` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`presupuestoId` INTEGER NOT NULL, `mes` TEXT NOT NULL, `nivel` TEXT NOT NULL, " +
                "FOREIGN KEY(`presupuestoId`) REFERENCES `presupuestos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_alertas_presupuesto_presupuestoId_mes_nivel` " +
                "ON `alertas_presupuesto` (`presupuestoId`, `mes`, `nivel`)",
        )
    }
}
