<html>
<head>
<title> Album List</title>
</head>

<?php
 echo "<b>In ".$_GET[country];
 $country=$_GET[country];
 setcookie('country',$country,time()+(86400*30),'/');

 $connect = mysql_connect("localhost","root","letmesee55!");
 $db=mysql_select_db("album",$connect);
 
 $page= $_GET[page]; 
 if($page=='') $page =1;
 $list_num =8;
 $page_num=8;
 $offset= $list_num*($page-1);

 $query="select count(*) from photo where album='$country'";
 $result = mysql_query($query) or die (mysql_error());

 $row=mysql_fetch_row($result);
 $total_no=$row[0];

 $total_page=ceil($total_no/$list_num);
 $cur_num=$total_no - $list_num*($page-1);
 
 $query = "select * from photo where album='$country' order by date desc limit $offset, $list_num";
 $result= mysql_query($query) or die(mysql_error()); 

?>

<form action="upload_photo.php" method="POST">
<input type="hidden" name="where" value=<?php echo $country; ?> />

<input type="submit" value="사진추가"/>
</form>

<?php
$i=0;
 echo "<hr><br />";
 echo "<table border='1' align='center'><tr>";
 while($array=mysql_fetch_array($result)){
	$date =date("Y/m/d", $array[writetime]);
	$where = "img/".$array[album]."/";
	$name = $array[title];
	if($i=="4"){ echo "<tr>"; $i=0;}
	echo "<td> <a href='view.php?country=$country&title=$name&page=$page'><img src='./img/$country/$name.jpg' width='200' height='200'/></a></td>";
	$cur_num--; $i++;	
 }
 echo "</tr> </table>";
?> 


<?php 

//여기서부터 각종 페이지 링크 

//먼저, 한 화면에 보이는 블록($page_num 기본값 이상일 때 블록으로 나뉘어짐 ) 

$total_block=ceil($total_page/$page_num); 

$block=ceil($page/$page_num); //현재 블록 

 

$first=($block-1)*$page_num; // 페이지 블록이 시작하는 첫 페이지 

$last=$block*$page_num; //페이지 블록의 끝 페이지 

 

if($block >= $total_block) { 

 $last=$total_page; 

} 

 

echo "&nbsp; <p align=center>"; 

//[처음][*개앞] 

if($block > 1) { 

 $prev=$first-1; 

echo "<a href='list.php?country=$country&page=1'>[처음 ]</a>&nbsp; "; 

echo "<a href='list.php?country=$country&page=$prev'>[$page_num 개 앞]</a>"; 


} 
//[이전] 

if($page > 1) { 

 $go_page=$page-1; 

echo " <a href='list.php?country=$country&page=$go_page'>[이전 ]</a>&nbsp; "; 

} 

 

//페이지 링크 

for ($page_link=$first+1;$page_link<=$last;$page_link++) { 

if($page_link==$page) { 

echo "<font color=green><b>$page_link</b></font>"; 


 } 

else { 

echo "<a href='list.php?country=$country&page=$page_link'>[$page_link]</a>"; 

 } 

} 


//[다음] 

if($total_page > $page) { 

 $go_page=$page+1; 

echo "&nbsp;<a href='list.php?country=$country&page=$go_page'>[다음]</a>"; 

} 

 

//[*개뒤][마지막] 

if($block < $total_block) { 

 $next=$last+1; 

echo "<a href='list.php?country=$country&page=$netxt'>[$page_num 개 뒤]</a>&nbsp;"; 

echo "<a href='list.php?country=$country&page=$total_page'>[마지막]</a></p>"; 

} 

?>

<form name="main" method="POST" action="index.php">
<input type="submit" value="Back">

</form>

