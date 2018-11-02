<html>
<head>
<title> Album View</title>
</head>

<?php
 echo "<b><p aligh='center'>In ".$_GET[country]."</b></p><hr>";
 $country=$_GET[country];
 $title = $_GET[title];
 $page = $_GET[page];

 $connect = mysql_connect("localhost","root","letmesee55!");
 $db=mysql_select_db("album",$connect);
 
 $query = "select * from photo where album='$country' and title='$title'";
 $result = mysql_query($query) or die(mysql_error());
 $array = mysql_fetch_array($result);

 $array[title]= stripslashes($array[title]);
 $array[memo]= stripslashes($array[memo]);

 echo "<table border ='1' aligh='center'>";
 echo "<tr><br> Title: ".$title."</tr></br>";
 echo "Memo: ".$array[memo];
 echo "<td><a href='list.php?country=$country&page=$page'><img src='./img/$country/$title.jpg' height='500'/></a></td>";

 echo"</table>";
 

 echo " <br /><a href='list.php?country=$country&page=$page'>목록으로</a>";
 echo "      ";
 echo "<a href='delete.php?country=$country&title=$title'>삭제</a>";
 echo "      ";

 echo "<a href='update_photo.php?country=$country&title=$title'>수정</a>";

?>

