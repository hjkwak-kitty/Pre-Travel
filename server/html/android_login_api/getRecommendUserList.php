<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['text_uid'])&&isset($_POST['con_title'])) {	
	 // receiving the post params
	$text_uid = $_POST['text_uid'];
	$con_title = $_POST['con_title'];
	$recommendList = $db->getRecommendUserList($text_uid, $con_title);
	//echo $recommendList['recommendList'];
	if ($recommendList) {
		// user already existed
        	$response["error"] = FALSE;
        	$response["recommendList"] = $recommendList['recommendList'];
        	echo json_encode($response);
	}else {
		$response["error"] = TRUE;
		$response["error_msg"] = "can't bring the list of user who put recomendation";
		//$response["recommendList"] = FALSE;
        	echo json_encode($response);
	}
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email is missing!";
    echo json_encode($response);
}
?>
