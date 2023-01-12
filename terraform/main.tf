#https://github.com/brikis98/infrastructure-as-code-talk

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "2.70.0"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = ">= 2.16.0"
    }
  }
}

provider "aws" {
  region = var.REGION
  shared_credentials_files = [var.CREDENTIAL_FILE_PATH]
  profile = var.AWS_PROFILE
}

module "docker_registry" {
  source = "./docker_registry"
  app_environment = var.ENV
  app_name = var.APP_NAME
  docker_image_path = "../Dockerfile"
}

module "aws_cluster" {
  source = "./cluster"
  app_environment = var.ENV
  app_name = var.APP_NAME
  size = 6
}

