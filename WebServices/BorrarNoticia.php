<?php
/**
 * Borra de la tabla el registro con el id recibido.
 */
include 'ConexionBaseDatos.php';
$json = array();
if(isset($_REQUEST["id"])){
	$id = $_REQUEST['id'];
	
	$consulta = "DELETE FROM noticias WHERE id = '".$id."'";

	$resultado = mysqli_query ($conexion, $consulta) or die (mysqli_error());

	if($resultado){
		echo "Eliminado";
		} else{
			echo "NoEliminado";
		}
} else{
		echo "DatoNoRecibido";
}
mysqli_close($conexion);
?>
