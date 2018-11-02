
<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

//json response array
$response =array("error" =>FALSE);

if(isset($_POST['keyword'])){
 	$keyword = $_POST['keyword'];
 	//$keyword="박쥐";
 	$_url = $_url = "https://apis.daum.net/search/book?apikey=c4a0d664eecbbed85b6f024326641079&q=".$keyword."&output=json&result=20";


	$ch = curl_init();

    	curl_setopt($ch, CURLOPT_URL, $_url);
    	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
    	curl_setopt($ch, CURLOPT_HEADER, FALSE); // http header return 삭제
    	curl_setopt($ch, CURLOPT_USERAGENT,$_SERVER['HTTP_USER_AGENT']);
    	curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE); // 텍스트로 받음

	$_data   = curl_exec($ch); 
	curl_close($ch);
	//echo $_data;


	/*json Array로 변환*/
	$jsonData = json_decode($_data,TRUE);
	//print "SL<br/><br/>";
	foreach($jsonData as $keySL=>$valueSL) { 
	  extract($jsonData); 
 	  //print "'$keySL' => '$valueSL'<br/>";
  	  if(is_array($valueSL)) { 
   	    // print "<br/>SLL - itemList Array";
   	     foreach($valueSL as $keySLL=>$valueSLL) { 
   	     if(is_array($valueSLL)) { 
      	        //echo "<br/><br/>"; 
     	        foreach($valueSLL as $key3=>$value3) { 
      	          //print "'$key3' => '$value3'<br/>";
    	        }
	        //echo "<br/>";      
	      } 
	   } 
	 } 
  	}

	$totalCount= $jsonData[channel][result];

	if($totalCount==0){
	 // required post params is missing
          $response["error"] = TRUE;
          $response["error_msg"] = "책이 없습니다.";
          echo json_encode($response);
	}else{
	  $response[totalCount]=$totalCount;
	  for($i=0;$i<$totalCount;$i++){
	     $response[title][$i]=strip_tags(htmlspecialchars_decode($jsonData[channel][item][$i][title]));
	     $response[author][$i]=$jsonData[channel][item][$i][author_t];
	     $response[publish][$i]=$jsonData[channel][item][$i][pub_nm];
	     $response[category][$i]=$jsonData[channel][item][$i][category];
	     $response[description][$i]=$jsonData[channel][item][$i][description];
	     $response[cover][$i]=$jsonData[channel][item][$i][cover_l_url];
	   //echo $response[title][$i]."#########33";
	   //echo $movie[$date][$i];
	   //echo $jsonData[channel][item][$i][title][0][content];
	}
	$response["error"] = FALSE;
	echo json_encode($response);
	//내부구조확인
	
}
} else{

 	// required post params is missing
   	$response["error"] = TRUE;
   	$response["error_msg"] = "Required parameters email or password is missing!";
   	echo json_encode($response);

}

/*
//내부구조확인
echo "<xmp>";
print_r($jsonData);
echo "</xmp>";*/
?>


