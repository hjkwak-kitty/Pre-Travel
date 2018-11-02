<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['email']) && isset($_POST['authText']) && isset($_POST['salt'])) {

	 // receiving the post params
	$email = $_POST['email'];
	$salt = $_POST['salt'];
	$authText= $_POST['authText'];

	$temp = substr($email,0,10);
	$origin="hi_". $temp."_bye";
	
	// get the user by email and password
	$middle =  $db->checkAuth($origin,$salt);
	if ($middle != false) {
		$finalText = substr($middle["encrypted"],0,6);
		if($finalText == $authText){
			$response["error"]= FALSE;
			$response["finalText"] = $finalText;
			echo json_encode($response);
		}
		else{
			 //인증번호 틀림
                        $response["error"] = TRUE;
                        $response["error_msg"] = "인증번호가 틀림니다";
                        echo json_encode($response);
		}	
	} else{
		//디코딩 실패
		$response["error"] = TRUE;
        	$response["error_msg"] = "인증문자 확인  실패.";
        	echo json_encode($response);
    	}


} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters is missing!";
    echo json_encode($response);
}
?>
