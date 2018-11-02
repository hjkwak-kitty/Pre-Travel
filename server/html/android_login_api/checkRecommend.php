<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['text_uid'])&&isset($_POST['con_title'])&&isset($_POST['user_name'])) {
	
	 // receiving the post params
	$user_name = $_POST['user_name'];
	$text_uid = $_POST['text_uid'];
	$con_title = $_POST['con_title'];

	if ($db->isUserPutRecommend($text_uid, $con_title, $uer_name)) {
		// user already existed
        	$response["error"] = FALSE;
        	$response["recommend"] = TRUE;
        	echo json_encode($response);
	}else {
		$response["error"] = FALSE;
		$response["recommend"] = FALSE;
        	echo json_encode($response);
	}
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email is missing!";
    echo json_encode($response);
}
?>
