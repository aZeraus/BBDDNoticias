<?php
/**
 * Envía resultados de la consulta a la tabla en formato JSON.
 */
include 'ConexionBaseDatos.php';

$consulta = "SELECT * FROM noticias";
$resultado = mysqli_query ($conexion, $consulta) or die (mysqli_error());
if($resultado){
	while ($results = mysqli_fetch_array($resultado)){
		$json['noticia'][] = array_map('utf8_encode', $results);
		echo json_encode($json);
	}
} else{
	echo "NoListado";
	//$json["documento"]=0;
	//$json["nombre"]='no registra';
	//$json["profesion"]='no registra';
}
mysqli_close($conexion);
?>