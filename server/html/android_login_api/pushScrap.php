<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_name'])&&isset($_POST['scrap_uid'])&&isset($_POST['scrap_title'])) {
	
	 // receiving the post params
	$user_name = $_POST['user_name'];
	$scrap_uid = $_POST['scrap_uid'];
	$scrap_title = $_POST['scrap_title'];

	if ($db->pushScrap($scrap_uid, $scrap_title, $user_name)) {
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
