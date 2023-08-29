function jwtExpireTime(token) {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload).exp;
}

// ACCESS 토큰 재발급 해서 로컬 스토리지에 저장하는 함수
async function reissueJwt(token){
    await fetch("/users/reissue-token",{
        method: "GET",
        headers:{
            "Authorization": "Bearer " + token
        }
    })
        .then(function (response){
            return response.json()
        })
        .then(function (json){
            if(json.token != undefined){
                const jwt = json.token;
                localStorage.setItem("token", jwt);
            }
            else location.href = "/login"
        })
}

// ACCESS 토큰이 유효한 지 확인하고 유효하지 않으면 재발급 후 로컬 스토리지에 저장해주는 함수
async function isValidateToken(token){
    const jwtExpire = await jwtExpireTime(token)
    const now = new Date().getTime() / 1000
    if(jwtExpire < now){
        await reissueJwt(token)
    }
}

export {jwtExpireTime, reissueJwt, isValidateToken};
