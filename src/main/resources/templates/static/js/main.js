import {isValidateToken, reissueJwt, jwtExpireTime} from "./keep-access-token.js";


document.getElementById("btn-login-form").addEventListener("click", function(){
    location.href = "/login"
})

document.getElementById("btn-start-matching").addEventListener("click", startMatching)

document.getElementById("btn-matching-accept").addEventListener("click",function(){
    webSocket.send("accept")
})

document.getElementById("btn-matching-reject").addEventListener("click",function(){
    webSocket.send("reject")
})

let webSocket;

// 매칭 시작 함수
async function startMatching() {
    await isValidateToken(localStorage.getItem("token"));
    const token = localStorage.getItem("token")
    const mode = document.querySelector('input[name="mode"]:checked').value;
    const myLine = document.querySelector('input[name="myLine"]:checked').value;
    const duoLine = document.querySelector('input[name="duoLine"]:checked').value;

    if (myLine == duoLine) {
        alert("같은 역할군 간의 매칭은 불가능합니다.")
        return;
    }

    webSocket = await new SockJS(`ws://localhost:8080/ws/matching?Authorization=${token}&mode=${mode}&myLine=${myLine}&duoLine=${duoLine}`);
    webSocket.onmessage = (msg) => {
        console.log(msg)
        try {
            const data = JSON.parse(msg.data)
            const chatMessage = document.createElement("div")
            const message = document.createElement("p")
            // message.innerText = data.username + ": " + data.message
            message.innerText = "---------- 상대방 정보 ----------\n" +
                "닉네임: " + data.nickname + "\n" +
                "티어: " + data.tier + "\n" +
                "모드: " + data.mode + "\n" +
                "라인: " + data.myLine + "\n"
            ;

            chatMessage.appendChild(message)
            document.getElementById("div-matching").appendChild(chatMessage)
        } catch (e) {
            const data = msg.data
            const chatMessage = document.createElement("div")
            const message = document.createElement("p")
            // message.innerText = data.username + ": " + data.message
            message.innerText = data;
            ;

            chatMessage.appendChild(message)
            document.getElementById("div-matching").appendChild(chatMessage)
        }
    }
}