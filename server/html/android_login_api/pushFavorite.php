<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_name'])&&isset($_POST['favorite_country'])&&isset($_POST['favorite_city'])) {
	
	 // receiving the post params
	$user_name = $_POST['user_name'];
	$favorite_country = $_POST['favorite_country'];
	$favorite_city = $_POST['favorite_city'];

	if ($db->pushFavorite($favorite_country, $favorite_city, $user_name)) {
		// user already existed
        	$response["error"] = FALSE;
        	$response["favorite"] = TRUE;
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
