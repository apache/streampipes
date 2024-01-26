package StatuCode

var (
	Unauthorized = Add(401, "The StreamPipes Backend returned an unauthorized error.\nPlease check your user name and/or password to be correct.")
	AccessDenied = Add(403, "There seems to be an issue with the access rights of the given user and the resource you queried.\n"+
		"Apparently, this user is not allowed to query the resource.\n"+
		"Please check the user's permissions or contact your StreamPipes admin.")
	NotFound         = Add(404, "There seems to be an issue with the Python Client calling the API inappropriately.")
	MethodNotAllowed = Add(405, "There seems to be an issue with the Python Client calling the API inappropriately.")
)
