<?php
/**
 * Inserta en la tabla el Titulo, Enlace y Fecha recibidos.
 */
include 'ConexionBaseDatos.php';
$json = array();

if(isset($_REQUEST["titulo"]) && isset($_REQUEST["enlace"]) && isset($_REQUEST["fecha"])){
	$titulo = $_REQUEST['titulo'];
	$enlace = $_REQUEST['enlace'];
	$fecha = $_REQUEST['fecha'];

	$consulta = "INSERT INTO noticias(Titulo, Enlace, Fecha) VALUES('".$titulo."','".$enlace."','".$fecha."')";
	$resultado = mysqli_query ($conexion, $consulta) or die (mysqli_error());
	if($resultado){
		echo "Insertada";
		} else{
			echo "NoInsertada";
		}
} else{
		echo "DatoNoRecibido";
}
mysqli_close($conexion);
?>