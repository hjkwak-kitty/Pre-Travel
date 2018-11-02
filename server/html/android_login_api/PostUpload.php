<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);
$size = $_POST['size'];

for($i=0;$i<$size;$i++){
  $contents["user_name"][$i]=$_POST["user_name_".$i];
  $contents["text_uid"][$i]=$_POST["text_uid_".$i];
  $contents["country"][$i] = $_POST["country_".$i];
  $contents["city"][$i] = $_POST["city_".$i];
  $contents["con_title"][$i] = $_POST["con_title_".$i];
  $contents["con_data1"][$i] = $_POST["con_data1_".$i];
  $contents["con_data2"][$i] = $_POST["con_data2_".$i];
  $contents["con_data3"][$i] = $_POST["con_data3_".$i];
  $contents["con_data4"][$i] = $_POST["con_data4_".$i];
  $contents["con_photo"][$i] = $_POST["con_photo_".$i];
}

if($contents){
 // $save=TRUE;
  $save = $db->uploadPost($contents,$size);
  if($save){	
	$response["error"]=FALSE;
	$response["test"]=$contents["country"][1];
	//$response["test"]=$size;
	echo json_encode($response);


  }else{
	$response["error"]=TRUE;
	$response["error_msg"]="실패!";
	echo json_encode($response);
  }

}else{
  $response["error"]=TRUE;
  $response["error_msg"] = "Required parameters is missing!";
  echo json_encode($response);
}
?>
