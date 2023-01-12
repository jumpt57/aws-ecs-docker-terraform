output "ecr_repo_id" {
  value = aws_ecr_repository.ecr_repo.id
}

output "docker_image_name" {
  value = docker_registry_image.my_docker_image.id
}