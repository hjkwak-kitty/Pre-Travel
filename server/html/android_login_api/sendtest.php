<?php

require_once("/downloads/PHPMailer_5.2.4/class.phpmailer.php");

 

$mail = new PHPMailer(true);

$mail->IsSMTP();
//$mail -> SMTPDebug = 4;


try {

  $mail->Host = "smtp.gmail.com";    // email 보낼때 사용할 서버를 지정 

  $mail->SMTPAuth = true;              // SMTP 인증을 사용함

  $mail->Port = 465;                        // email 보낼때 사용할 포트를 지정

  $mail->SMTPSecure = "ssl";        // SSL을 사용함

  $mail->Username   = "hjkwak91@gmail.com";    // Gmail 계정

  $mail->Password   = "letmesee55";            // 패스워드

 

  $mail->SetFrom('hjkwak91@gmail.com', 'Hyojin'); // 보내는 사람 email 주소와 표시될 이름 (표시될 이름은 생략가능)

  $mail->AddAddress('hjkwak91@gmail.com', 'You'); // 받을 사람 email 주소와 표시될 이름 (표시될 이름은 생략가능)

  $mail->Subject = 'Email Subject';        // 메일 제목

  $mail->MsgHTML("Email Content");    // 메일 내용 (HTML 형식도 되고 그냥 일반 텍스트도 사용 가능함)

 

  $mail->Send();                                // 실제로 메일을 보냄

  echo "Message Sent OK<p></p>\n";

} catch (phpmailerException $e) {

  echo $e->errorMessage(); //Pretty error messages from PHPMailer

} catch (Exception $e) {

  echo $e->getMessage(); //Boring error messages from anything else!

}
?>
