const apiHandler = {
    async fetchApi(method, url, params, success, fail) {
        try {
            let options = {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                },
            };

            if (method === "GET" && params) {
                const queryString = new URLSearchParams(params).toString();
                url += (url.includes("?") ? "&" : "?") + queryString;
            } else if (params && ["POST", "PUT", "PATCH", "DELETE"].includes(method)) {
                options.body = JSON.stringify(params);
            } else {
                throw new Error();
            }

            const apiResponse = await fetch(url, options);

            if (!apiResponse.ok) {
                const status = apiResponse.status;
                let responseData;
                try {
                    responseData = await apiResponse.json();
                } catch {
                    responseData = { };
                }

                const message = responseData.message || `HTTP error! status: ${status}`;
                const data = responseData.data || null;

                switch (status) {
                    case 400:
                        fail(message || "잘못된 요청입니다.", data);
                        break;
                    case 401:
                        alert("로그인이 필요합니다.");
                        window.location.href = "/login";
                        break;
                    case 403:
                        alert("접근 권한이 없습니다.");
                        break;
                    case 404:
                        alert("요청하신 데이터를 찾을 수 없습니다.");
                        break;
                    case 500:
                        alert("서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요.");
                        throw error;
                    default:
                        alert(`오류가 발생했습니다. (HTTP ${status})`);
                }
            }

            const responseData = await apiResponse.json();

            let code = responseData['code'];
            let message = responseData['message'];
            let data = responseData['data'];

            console.log("code: " + code);
            console.log("message: " + message);
            console.log("data:", data);

            if (code === 200) {
                success(data);
            } else {
                if (fail) {
                    fail(message, data);
                } else {
                    alert(message || "요청 처리 중 오류가 발생했습니다.");
                }
            }
        } catch (error) {
            console.log('error: ' + error);
            alert("데이터 조회 중 오류가 발생했습니다.\n관리자에게 문의해주세요");
        }
    },
    /** GET 요청 */
    fetchApiGet(url, params, success, fail) {
        this.fetchApi("GET", url, params, success, fail);
    },
    /** POST 요청 */
    fetchApiPost(url, params, success, fail) {
        this.fetchApi("POST", url, params, success, fail);
    },
    /** PUT 요청 */
    fetchApiPut(url, params, success, fail) {
        this.fetchApi("PUT", url, params, success, fail);
    },
    /** PATCH 요청 */
    fetchApiPatch(url, params, success, fail) {
        this.fetchApi("PATCH", url, params, success, fail);
    },
    /** DELETE 요청 */
    fetchApiDelete(url, params, success, fail) {
        this.fetchApi("DELETE", url, params, success, fail);
    },
}

const paramHandler = {
    formToObject(formTag) {
        const formData = new FormData(formTag);
        const param = {}
        for (const [key, value] of formData.entries()) {
            if (param[key]) {
                if (Array.isArray(param[key])) {
                    param[key].push(value);
                } else {
                    param[key] = [param[key], value];
                    param[key] = value;
                }
            }
        }
        return param;
    }
}