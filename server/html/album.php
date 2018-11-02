<html>
<head>
<title> My Album</title>
</head>

<?php
 echo "In ".$_GET[country];
 $country=$_GET[country];
 setcookie('country',$country,time()+(86400*30),'/');

 $connect = mysql_connect("localhost","root","letmesee55!");
 $db=mysql_select_db("album",$connect);
 
 $getID="select image_num from album where id='$country'";
 $getID=mysql_query($getID);
 $getID=mysql_fetch_array($getID);

 $getDir="select directory from album where id='$country'";
 $getDir=mysql_query($getDir);
 $getDir=mysql_fetch_array($getDir);

 $where =$getDir['directory'];
 if($country =='etc'){
  $where ='/var/www/html/img/etc';
}
 $img_num=$getID['image_num'];

 if($img_num==0){
  if($country!='etc'){
  echo "  ->앨범에  사진이 없습니다.";
 }}
 else{
  echo " ->앨범에 사진이 ";
  echo $img_num;
  echo "장  있습니다.";}

?>

<form action="upload_photo.php" method="POST">
<input type="hidden" name="where" value=<?php echo $country; ?> />

<input type="submit" value="사진추가"/>
</form>

<br /> 
<?php
if ($handle = opendir($where)) { 

   /* This is the correct way to loop over the directory. */ 

   while (false !== ($entry = readdir($handle))) { 

      if ($entry != '.' && $entry != '..'){ 

         if(strchr($entry, ".jpg") == true){ //$entry변수에 .jpg라는 문자가 있는지 확인 

            echo "<br /><div>파일명 : $entry</div><br />"; 

            echo "<div><img src='./img/$country/$entry' /></div>"; 

         } 

      } 

   } 

   closedir($handle); 

} 

?> 

