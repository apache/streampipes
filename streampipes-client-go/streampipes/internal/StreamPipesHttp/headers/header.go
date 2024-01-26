package headers

import (
	"net/http"
)

type Headers struct {
	Req    *http.Request
	Header http.Header
}

//func (h *Headers) AuthorizationBearer(bearerToken string) StreamPipesHttp.Header {
//	h.Header = h.Req.Header // h.Header = StreamPipesHttp.Header{}
//	h.Header.Set("Authorization", "Bearer "+bearerToken)
//	return h.Header
//}

func (h *Headers) XApiKey(apiKey string) http.Header {
	h.Header = h.Req.Header
	h.Header.Set("X-API-KEY", apiKey)
	return h.Header
}

func (h *Headers) XApiUser(apiUser string) http.Header {
	h.Header = h.Req.Header
	h.Header.Set("X-API-USER", apiUser)
	return h.Header
}

func (h *Headers) AcceptJson() http.Header {
	h.Header = h.Req.Header
	h.Header.Set("Accept", "application/json")
	return h.Header
}

func (h *Headers) ContentTypeJson() http.Header {
	h.Header = h.Req.Header
	h.Header.Set("Content-type", "application/json")
	return h.Header
}

//func MakeHeader(name, value string) StreamPipesHttp.Header {
//	//Used to add request header messages
//}
