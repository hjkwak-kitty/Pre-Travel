<?php
header("Content-Type:text/html;charset=utf-8");
        require_once 'include/DB_Functions.php';
        $db = new DB_Functions();

//json response array
        $response =array("error" =>FALSE);

        if(isset($_GET['keyword'])){
        $keyword = $_GET['keyword'];
        $keyword="박쥐";
        $_url = "https://apis.daum.net/contents/movie?apikey=c4a0d664eecbbed85b6f024326641079&q=".$keyword."&output=json&result=20";

        $ch = curl_init();

        curl_setopt($ch, CURLOPT_URL, $_url);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
        curl_setopt($ch, CURLOPT_HEADER, FALSE); // http header return 삭제
        curl_setopt($ch, CURLOPT_USERAGENT,$_SERVER['HTTP_USER_AGENT']);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE); // 텍스트로 받음

        $_data   = curl_exec($ch);
        curl_close($ch);
       // echo $_data;


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

        $totalCount= $jsonData[channel][totalCount];

        if($totalCount==0){
        // required post params is missing
        $response["error"] = TRUE;
        $response["error_msg"] = "영화가 없습니다.";
        echo json_encode($response,JSON_UNESCAPED_UNICODE);
        }else{
        $response[totalCount]=$totalCount;
        for($i=0;$i<$totalCount;$i++){
        $response[title][$i]=strip_tags(htmlspecialchars_decode($jsonData[channel][item][$i][title][0][content]));
        $response[director][$i]=$jsonData[channel][item][$i][director][0][content];
        $response[actor][$i]=$jsonData[channel][item][$i][actor][0][content];
        $response[genre][$i]=$jsonData[channel][item][$i][genre][0][content]." ".$jsonData[channel][item][$i][genre][1][content];
        $response[moreinfo][$i]=$jsonData[channel][item][$i][story][0][content];
        $response[poster][$i]=$jsonData[channel][item][$i][thumbnail][0][content];
        $response[date][$i]=$jsonData[channel][item][$i][year][0][content];
        $response[age][$i]=$jsonData[channel][item][$i][open_info][1][content];
        $response[time][$i]=$jsonData[channel][item][$i][open_info][2][content];
	$response[eng_title][$i]=$jsonData[channel][item][$i][eng_title][0][content];
	//echo $response[title][$i]."#########33";
        //echo $movie[$date][$i];
        //echo $jsonData[channel][item][$i][title][0][content];
        }
        $response["error"] = FALSE;
        echo json_encode($response,JSON_UNESCAPED_UNICODE);
        //내부구조확인

        }
        } else{

        // required post params is missing
        $response["error"] = TRUE;
        $response["error_msg"] = "Required parameters email or password is missing!";
        echo json_encode($response,JSON_UNESCAPED_UNICODE);

        }


//내부구조확인
echo "<xmp>";
print_r($jsonData);
echo "</xmp>";
        ?>
