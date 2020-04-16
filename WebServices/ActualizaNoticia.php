<?php
/**
 * Actualiza el registro con el id recibido, modificando los valores Leido, Favorita y Fuente.
 */
include 'ConexionBaseDatos.php';
$json = array();
if(isset($_REQUEST["id"]) && isset($_REQUEST["leido"]) && isset($_REQUEST["favorita"]) && isset($_REQUEST["fuente"])){
	$id = $_REQUEST['id'];
	$leido = $_REQUEST['leido'];
	$favorita = $_REQUEST['favorita'];
	$fuente = $_REQUEST['fuente'];
	
	$consulta = "UPDATE noticias SET Leido = '".$leido."', Favorita = '".$favorita."', Fuente = '".$fuente."' WHERE id = '".$id."'";

	$resultado = mysqli_query ($conexion, $consulta) or die (mysqli_error());

	if($resultado){
		echo "Actualizado";
		} else{
			echo "NoActualizado";
		}
} else{
		echo "DatoNoRecibido";
}
mysqli_close($conexion);
?>