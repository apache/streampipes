package de.fzi.cep.sepa.model.client.user;

public class Info {

	private Authc authc;
	private Authz authz;
	
	public Info(Authc authc, Authz authz) {
		super();
		this.authc = authc;
		this.authz = authz;
	}

	public Info() {
		// TODO Auto-generated constructor stub
	}

	public Authc getAuthc() {
		return authc;
	}

	public void setAuthc(Authc authc) {
		this.authc = authc;
	}

	public Authz getAuthz() {
		return authz;
	}

	public void setAuthz(Authz authz) {
		this.authz = authz;
	}	
}
