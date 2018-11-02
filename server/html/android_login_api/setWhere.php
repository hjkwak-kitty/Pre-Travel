<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['where'])){
  // receiving the post params
  $where=$_POST['where'];

  $destination = $db->isWhereExisted($where);
  if($destination){
	$response["error"] = FALSE;
	$response["city"] = $destination[city];
	$response["country"] = $destination[country];
	$response["myplace"]= $destination[country].", ".$destination[city];
	echo json_encode($response);

  }else{
	// user is not found with the credentials
        $response["error"] = FALSE;
	$response["city"] = "null";
        $response["country"] = "null";
        $response["myplace"]= "null";
        echo json_encode($response);
 }
}else{
  // required post params is missing
   $response["error"] = TRUE;
   $response["error_msg"] = "Required parameters email or password is missing!";
   echo json_encode($response);
}


?>
