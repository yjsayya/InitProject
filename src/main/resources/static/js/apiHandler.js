function example() {
    const url = '/api/send';
    const params = {
        param1 : 'param1',
        param2 : 'param2',
        param3 : 'param3',
    }
    apiHandler.fetchApiPost(url, params,
        (data) => {
            console.log('성공', data);
        },
        (message, data) => {
            console.log('실패', message, data);
        }
    );
}

const apiHandler = {
    /** GET */
    async fetchApiGet(url, params, successHandler, failHandler) {
        const requestParam = params && Object.keys(params).length > 0 ? '?' + new URLSearchParams(params).toString() : '';
        try {
            const response = await fetch(url + requestParam, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });

            if (!response.ok) {
                throw new Error(response.status);
            }

            const responseData = await response.json();
            this._handleResponse(responseData, successHandler, failHandler);

        } catch (err) {
            this._handleError(err);
        }
    },

    /** POST, PUT, PATCH, DELETE */
    async fetchApi(method, url, params, successHandler, failHandler) {
        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(params),
            });

            if (!response.ok) {
                throw new Error(response.status);
            }

            const responseData = await response.json();
            this._handleResponse(responseData, successHandler, failHandler);

        } catch (err) {
            this._handleError(err);
        }
    },

    /** POST 요청 */
    fetchApiPost(url, params, success, fail) {
        this.fetchApi('POST', url, params, success, fail);
    },
    /** PUT 요청 */
    fetchApiPut(url, params, success, fail) {
        this.fetchApi('PUT', url, params, success, fail);
    },
    /** PATCH 요청 */
    fetchApiPatch(url, params, success, fail) {
        this.fetchApi('PATCH', url, params, success, fail);
    },
    /** DELETE 요청 */
    fetchApiDelete(url, params, success, fail) {
        this.fetchApi('DELETE', url, params, success, fail);
    },

    _handleResponse(responseData, successHandler, failHandler) {
        const { code, message, data } = responseData;
        if (code === 200) {
            successHandler(data);
        } else {
            failHandler(message, data);
        }
    },

    _handleError(err) {
        const status = Number(err.message);
        switch (status) {
            case 400:
                alert('400 에러 발생');
                break;
            case 401:
                alert('401 에러 발생');
                break;
            case 403:
                alert('403 에러 발생');
                break;
            case 404:
                alert('404 에러 발생');
                break;
            case 500:
                alert('오류가 발생했어요.\n관리자에게 문의해주세요');
                break;
            default:
                break;
        }
    },
}

const pagingHandler = {
    calculatePageBlockRange(currentPage, totalPage) {
        const PAGE_SIZE = 5;
        if (totalPage <= PAGE_SIZE) {
            return { start: 1, end: totalPage };
        }
        // 초반 영역 (1 ~ 3)
        if (currentPage <= 3) {
            return { start: 1, end: PAGE_SIZE };
        }
        // 끝 영역 (n, n-1, n-2)
        if (currentPage >= totalPage - 2) {
            return { start: totalPage - (PAGE_SIZE - 1), end: totalPage };
        }
        // 일반 구간 : 현재 페이지를 가운데(3번째)로
        return { start: currentPage - 2, end: currentPage + 2 };
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