# MANDATORY VARIABLES
TF_VAR_ENV=(aws configure get environment --output text --profile $AWS_PROFILE)
TF_VAR_REGION=(aws configure get region --output text  --profile $AWS_PROFILE)
TF_VAR_AWS_PROFILE=($AWS_PROFILE)
TF_VAR_CREDENTIAL_FILE_PATH=($AWS_CREDENTIAL_PATH)
TF_VAR_APP_NAME="devops-urlshortener-app"

install:
	@mvn install -DskipTests

package:
	@mvn package -D(ENV)

format-terraform:
	@terraform fmt

lint-terraform:
	@terraform validate

deploy: lint-terraform
	@terraform apply

synth:
	@terraform show