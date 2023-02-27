// 모달 스크립트
// Modal을 가져옵니다.
let modalss = document.getElementsByClassName("modall");
// Modal을 띄우는 클래스 이름을 가져옵니다.
let btnss = document.getElementsByClassName("headerBtn");
// Modal을 닫는 close 클래스를 가져옵니다.
let spaness = document.getElementsByClassName("closed");
let funcss = [];

// Modal을 띄우고 닫는 클릭 이벤트를 정의한 함수
function Modal(num) {
    return function () {
        // 해당 클래스의 내용을 클릭하면 Modal을 띄웁니다.
        btnss[num].onclick = function () {
            modalss[num].style.display = "block";

            console.log(num);

        };

        // <span> 태그(X 버튼)를 클릭하면 Modal이 닫습니다.
        spaness[num].onclick = function () {
            modalss[num].style.display = "none";

        };
    };
}

// 원하는 Modal 수만큼 Modal 함수를 호출해서 funcs 함수에 정의합니다.
for (let i = 0; i < btnss.length; i++) {
    funcss[i] = Modal(i);
}

// 원하는 Modal 수만큼 funcs 함수를 호출합니다.
for (let j = 0; j < btnss.length; j++) {
    funcss[j]();
}

// Modal 영역 밖을 클릭하면 Modal을 닫습니다.
window.onclick = function (event) {
    if (event.target.className == "modall") {
        event.target.style.display = "none";

    }
};

// 이미지 프리뷰
function readURL(input) {
    if (input.files && input.files[0]) {
        let reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById('preview').src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    } else {
        document.getElementById('preview').src = "";
    }
}

// 신고시 알림을 위한 함수
function checkConfirm(){
    alert("신고가 처리되면 이메일로 알려드립니다.");
    return true;
}

// Targets all textareas with class "txta"
let textareas = document.querySelectorAll('.txta'),
    hiddenDiv = document.createElement('div'),
    content = null;

// Adds a class to all textareas
for (let j of textareas) {
    j.classList.add('txtstuff');
}

// Build the hidden div's attributes

// The line below is needed if you move the style lines to CSS
// hiddenDiv.classList.add('hiddendiv');

// Add the "txta" styles, which are common to both textarea and hiddendiv
// If you want, you can remove those from CSS and add them via JS
hiddenDiv.classList.add('txta');

// Add the styles for the hidden div
// These can be in the CSS, just remove these three lines and uncomment the CSS
hiddenDiv.style.display = 'none';
hiddenDiv.style.whiteSpace = 'pre-wrap';
hiddenDiv.style.wordWrap = 'break-word';

// Loop through all the textareas and add the event listener
for(let i of textareas) {
    (function(i) {
        // Note: Use 'keyup' instead of 'input'
        // if you want older IE support
        i.addEventListener('input', function() {

            // Append hiddendiv to parent of textarea, so the size is correct
            i.parentNode.appendChild(hiddenDiv);

            // Remove this if you want the user to be able to resize it in modern browsers
            i.style.resize = 'none';

            // This removes scrollbars
            i.style.overflow = 'hidden';

            // Every input/change, grab the content
            content = i.value;

            // Add the same content to the hidden div

            // This is for old IE
            content = content.replace(/\n/g, '<br>');

            // The <br ..> part is for old IE
            // This also fixes the jumpy way the textarea grows if line-height isn't included
            hiddenDiv.innerHTML = content + '<br style="line-height: 3px;">';

            // Briefly make the hidden div block but invisible
            // This is in order to read the height
            hiddenDiv.style.visibility = 'hidden';
            hiddenDiv.style.display = 'block';
            i.style.height = hiddenDiv.offsetHeight + 'px';

            // Make the hidden div display:none again
            hiddenDiv.style.visibility = 'visible';
            hiddenDiv.style.display = 'none';
        });
    })(i);
}

