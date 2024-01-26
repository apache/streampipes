package StatuCode

type ErrorCode interface {
	Error() string
	Code() int
	Message() string
}

type Code struct {
	code int
	msg  string
}

//var _ ErrorCode = (*Code)(nil)

func (c *Code) Code() int {
	return c.code
}

func (c *Code) Message() string {
	return c.msg
}

func Add(code int, msg string) Code {
	return Code{code: code, msg: msg}
}
