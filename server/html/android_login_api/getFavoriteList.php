<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_name'])) {	
	 // receiving the post params
	$user_name = $_POST['user_name'];
	$favoriteList = $db->getScrapList($user_name);
		
	if ($favoriteList) {
		// user already existed
        	$response["error"] = FALSE;
        	$response["favorite_country"] = $favoriteList["favorite_country"];
		$response["favorite_city"] = $favoriteList["favorite_city"];
        	echo json_encode($response);
	}else {
		$response["error"] = TRUE;
		$response["error_msg"] = "can't bring the favorite List";
        	echo json_encode($response);
	}
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters are missing!";
    echo json_encode($response);
}
?>
