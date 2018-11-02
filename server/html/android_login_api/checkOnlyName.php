<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['name'])) {
	
	 // receiving the post params
	$name = $_POST['name'];

	if ($db->isUserExisted($name)) {
	// user already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "사용자가 이미 존재합니다." . $name;
        echo json_encode($response);
	}else {
	$response["error"] = FALSE;
        echo json_encode($response);
	}
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email is missing!";
    echo json_encode($response);
}
?>
