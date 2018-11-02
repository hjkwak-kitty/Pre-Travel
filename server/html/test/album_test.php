<html>
<head>

<title>My Album</title>
</head>

<?php
 echo "In ";
 echo $_GET[country];
 $country = $_GET[country];
?>
 <a href="insert.php"> '사진추가'</a>
<br>
<br>

<script>
 <!--
 // 속도를 설정 하세요 (1초=1000)
 var slideShowSpeed = 1500;

 // 겹쳐질 시간을 설정 하세요 (초)
 var crossFadeDuration = 1;

 var Pic = new Array();
 var country = '<?php echo $country;?>';


 // 사용할 이미지 파일을 설정
if(country == "cambodia"){
 Pic[0] = 'img/cambodia/1.jpg';
 Pic[1] = 'img/cambodia/2.jpg';
 Pic[2] = 'img/cambodia/3.jpg';
 Pic[3] = 'img/cambodia/4.jpg';
}
else if(country=='korea'){
 Pic[0] = 'img/korea/1.jpg';
 Pic[1] = 'img/korea/2.jpg';
 Pic[2] = 'img/korea/3.jpg';
 Pic[3] = 'img/korea/4.jpg';
}

 var t;
 var j = 0;
 var p = Pic.length;
 var preLoad = new Array();

 for (i = 0; i < p; i++)
 {
        preLoad[i] = new Image();
        preLoad[i].src = Pic[i];
 }

 function runSlideShow()
 {
        if (document.all)
        {
               document.images.SlideShow.style.filter="blendTrans(duration=2)";
               document.images.SlideShow.style.filter="blendTrans(duration=crossFadeDuration)";
               document.images.SlideShow.filters.blendTrans.Apply();
        }

        document.images.SlideShow.src = preLoad[j].src;

        if (document.all)
        {
               document.images.SlideShow.filters.blendTrans.Play();
        }

        j = j + 1;

        if (j > (p - 1))
        {
               j = 0;
        }

        t = setTimeout('runSlideShow()', slideShowSpeed);
 }
 </script>
 <body onLoad="runSlideShow();">
 <img src="" name='SlideShow' border='0'>
 </body>

</html>

