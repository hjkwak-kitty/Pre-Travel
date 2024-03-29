<?php

class GCMPushMessage{

	private $url = 'https://android.googleapis.com/gcm/send';
	private $serverApiKey = "";
	private $devices = array();
	private $notification_type;

	function GCMPushMessage($apiKeyIn, $is_notification){
		$this->serverApiKey = $apiKeyIn;
		$this->notification_type = $is_notification;
	//	echo $apiKeyIn.$is_notification;
	}
	
	function setDevices($deviceIds){
		if(is_array($deviceIds)){
			$this->devices = $deviceIds;
		} else{
			$this->devices = array($deviceIds);
		}
	}
	
	function send($title,$message,$extra){
		if(!is_array($this->devices) || count($this->devices)== 0){
			$this->error("No devices set");
		}
		if(strlen($this->serverApiKey) < 8){
			$this->error("Server API Key not set");
		}

		$fields = array(
			'registration_ids' => $this->devices,
			'data' => array('title' =>$title, 'message' => $message, 'notification_type' => $this->notification_type, 'extra' =>$extra),);

		//echo json_encode($fields);
		//exit;
	
			$headers =array(
				'Authorization: key =' . $this->serverApiKey,
				'Content-Type: application/json'
			);

			//Open connection
			$ch =curl_init();

			//Set te url, number of Post vars, POST data
 			curl_setopt($ch, CURLOPT_URL, $this->url);
			curl_setopt($ch, CURLOPT_POST, true);
			curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
			curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

		//Execute post
		$result = curl_exec($ch);

		//Close connection
		curl_close($ch);

		return $result;
	}

	function error($msg){
		echo "Android send notification failed with error:";
		echo "\t".$msg;
		exit(1);
	}
}
?>
