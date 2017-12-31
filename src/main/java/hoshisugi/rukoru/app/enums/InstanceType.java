package hoshisugi.rukoru.app.enums;

public enum InstanceType {

	T2Micro("t2.micro", com.amazonaws.services.ec2.model.InstanceType.T2Micro),
	T2Small("t2.small", com.amazonaws.services.ec2.model.InstanceType.T2Small),
	T2Medium("t2.medium", com.amazonaws.services.ec2.model.InstanceType.T2Medium),
	T2Large("t2.large", com.amazonaws.services.ec2.model.InstanceType.T2Large),
	M3Medium("m3.medium", com.amazonaws.services.ec2.model.InstanceType.M3Medium),
	M3Large("m3.large", com.amazonaws.services.ec2.model.InstanceType.M3Large);

	private final String displayName;
	private final com.amazonaws.services.ec2.model.InstanceType value;

	private InstanceType(String displayName, com.amazonaws.services.ec2.model.InstanceType value) {
		this.displayName = displayName;
		this.value = value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public com.amazonaws.services.ec2.model.InstanceType getValue() {
		return value;
	}
}
