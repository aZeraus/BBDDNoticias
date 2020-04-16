<?php
/**
 * Realiza la conexión a la BBDD usanto los datos de la conexión que están en DatosCon.php.
 */
require_once 'DatosCon.php';
$conexion=new mysqli(HOSTNAME,USERNAME,PASSWORD,DATABASE);
if($conexion->connect_errno){
    echo "El sitio web está experimentado problemas";
}
?>