<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_REQUEST['text_uid'])&&isset($_REQUEST['con_title'])&&isset($_REQUEST['user'])) {
	
	 // receiving the post params
	$user = $_REQUEST['user'];
	$text_uid = $_REQUEST['text_uid'];
	$con_title = $_REQUEST['con_title'];
	(int)$howTo = $_REQUEST['Recommend_state'];

	if ($db->pushRecommend($text_uid, $con_title, $user, $howTo)) {
		// user already existed
        	$response["error"] = FALSE;
        	$response["recommend"] = TRUE;
        	//$response["recommend"]= $con_title;
		echo json_encode($response);
	}else {
		$response["error"] = FALSE;
		$response["recommend"] = FALSE;
        	echo json_encode($response);
	}
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameter is missing!";
    echo json_encode($response);
}
?>
