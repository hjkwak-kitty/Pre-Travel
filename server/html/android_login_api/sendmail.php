<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['email'])) {
	
	 // receiving the post params
	$email = $_POST['email'];

	if ($db->isUserExisted($email)) {
	// user already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "사용자가 이미 존재합니다." . $email;
        echo json_encode($response);
	}else {

		$mailto = $email; 
		$temp = substr($email,0,10);
		$origin="hi_". $temp."_bye";


		// get the user by email and password
		$middle = $db->getAuthText($origin);

		if ($middle != false) {
			$finalText = substr($middle["encrypted"],0,6);
		
			$subject = "어플 가입 메일 인증 문자";
			$content= "어플 가입 메일 인증 문자: ".$finalText;
			$result=mail($mailto, $subject, $content);
			if($result){
				$response["error"]= FALSE;
				$response["salt"]= $middle["salt"];
				echo json_encode($response);
			} else{
				//메일발송 실패
	                	$response["error"] = TRUE;
        	        	$response["error_msg"] = "메일발송실패";
                		echo json_encode($response);
			}
		} else{
			//인증문자생성실패
			$response["error"] = TRUE;
     	   		$response["error_msg"] = "인증문자 생성 실패.";
        		echo json_encode($response);
    		}
	}	
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email is missing!";
    echo json_encode($response);
}
?>
