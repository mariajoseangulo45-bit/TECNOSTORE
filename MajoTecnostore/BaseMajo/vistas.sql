CREATE VIEW vw_clientes AS
    SELECT c.id, td.codigo AS tipo_doc, p.identificacion,
          CONCAT(p.nombre, ' ', p.apellido) AS cliente,
            p.correo, p.telefono, c.fecha_registro
     FROM clientes c
     JOIN personas p        ON p.id  = c.id_persona
     JOIN tipo_documento td ON td.id = p.id_tipo_documento;

     CREATE VIEW vw_empleados AS
     SELECT e.id, td.codigo AS tipo_doc, p.identificacion,
            CONCAT(p.nombre, ' ', p.apellido) AS empleado,
            p.correo, p.telefono, e.activo
     FROM empleado e
     JOIN personas p        ON p.id  = e.id_persona
     JOIN tipo_documento td ON td.id = p.id_tipo_documento;

     CREATE VIEW inventario AS
    -> SELECT c.id, m.nombre AS marca, c.modelo,
    ->        CONCAT(so.nombre, ' ', v.version) AS sistema_operativo,
    ->        g.nombre AS gama,
    ->        c.precio_unitario, c.precio_mayor, c.precio_especial, c.stock
    -> FROM celulares c
    -> JOIN marca m                     ON m.id  = c.id_marca
    -> JOIN version_sistema_operativo v ON v.id  = c.id_version_so
    -> JOIN sistema_operativo so        ON so.id = v.id_sistema_operativo
    -> JOIN gama g                      ON g.id  = c.id_gama;

    CREATE VIEW detalleCompleto AS
    -> SELECT v.id AS venta, v.fecha, vc.cliente, ve.empleado,
    ->        i.marca, i.modelo, d.cantidad,
    ->        tp.nombre AS tipo_precio_aplicado,
    ->        d.precio_unitario, d.precio_mayor, d.precio_especial,
    ->        d.subtotal, v.total
    -> FROM ventas v
    -> JOIN vw_clientes vc   ON vc.id = v.id_cliente
    -> JOIN vw_empleados ve  ON ve.id = v.id_empleado
    -> JOIN detalle_venta d  ON d.id_venta = v.id
    -> JOIN tipo_precio tp   ON tp.id = d.id_tipo_precio
    -> JOIN inventario i  ON i.id = d.id_celular;