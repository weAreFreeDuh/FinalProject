// 문학 게시글 페이

    let memCode = $('#loginId').val();




function checkLiterature(boaCode, category) {
    console.log(boaCode);
    console.log(category);
    console.log(memCode);

    if (category == "문학") {
        $.ajax({
            type: 'post',
            url: 'ajaxPayAndCheck',
            data: {
                "boaCode": boaCode,
                "memCode": memCode
            },
            dataType: "text",
            success:
                function (result) {
                    if (result != null) {
                        // result 0이면 꽁자, 가격이 나오면 이면 이미 결제해야함
                        // 사용할 수 있는 아이디
                        console.log("result!=null")
                        console.log("가격은?" + result);
                        if (result == 0) {
                            checkLoginNow();
                        } else if (result == 1) {
                            location.href = "DRA004?boaCode=" + boaCode;
                        } else {
                            checkIdAndPay(result);
                        }
                    } else {
                        // 사용할 수 없는 아이디
                        console.log("result==null")
                    }
                },
            error: function () {
                alert('ajaxPayAndCheck 실패');
            }
        })
    } else {
        location.href = "DRA004?boaCode=" + boaCode;
    }

    function asd() {
        console.log("이게 되나 시발???" + boaCode);
    }

    function checkIdAndPay(price) {
        if (!confirm("이 게시글의 가격은 " + price + "입니다 읽으시겠습니까?.")) {
            /*비동의*/
            alert("취소(아니오)를 누르셨습니다.");
        } else {
            /*동의*/
            checkLoginNow();
        }
    }

    function checkLoginNow() {
        console.log("checkLoginNow")

        console.log(memCode);
        if (memCode == null) {
            console.log('아이디 없음');
            if (!confirm("결제 진행을 위해 로그인 하셔야 합니다 로그인하시겠습니까?")) {
                /*비동의*/
                alert("취소되었습니다.");
            } else {
                /*동의*/
                location.href = 'join'
            }
        } else {/*결제 진행*/
            console.log('아이디 있음');
            /*결제 진행  ajax*/
            $.ajax({
                type: 'post',
                url: 'ajaxLitPay',
                data: {
                    "boaCode": boaCode,
                    "memCode": memCode
                },
                dataType: "text",
                success:
                    function (result) {
                        if (result == 1) {
                            /*적절히 성공할시 */
                            console.log('결제성공');
                            location.href = "DRA004?boaCode=" + boaCode;
                        } else {
                            /*돈이 없거나 */
                            /*등등*/
                            alert('포인트가 부족합니다');
                            location.href = 'Pay_PayMembership';
                            console.log('결제실패')
                        }
                    },
                error: function () {
                    alert('ajaxLitPay 실패');
                }
            })
        }
    }

}