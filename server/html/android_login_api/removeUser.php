<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_GET['user_name'])) {
	
	 // receiving the post params
	$user_name = $_GET['user_name'];
	
	if ($db->removeUser($user_name)) {
		// user already existed
        	$response["error"] = FALSE;
        	$response["recommend"] = TRUE;
        	echo json_encode($response);
	}else {
		$response["error"] = TRUE;
		$response["error_msg"] = "실패";
        	echo json_encode($response);
	}
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameter is missing!";
    echo json_encode($response);
}
?>
