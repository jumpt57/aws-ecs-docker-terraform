data "aws_vpc" "default" {
  default = true
}

data "aws_availability_zones" "available" {}

# Look up the default subnets in the AZs available to this account (up to a max of 3)
data "aws_subnet" "default" {
  count             = min(length(data.aws_availability_zones.available.names), 3)
  default_for_az    = true
  vpc_id            = data.aws_vpc.default.id
  availability_zone = element(data.aws_availability_zones.available.names, count.index)
}

resource "aws_ecs_cluster" "example_cluster" {
  name = "${var.app_name}-${var.app_environment}-cluster"
  tags = {
    Name        = "${var.app_name}-cluster"
    Environment = var.app_environment
  }
}

resource "aws_autoscaling_group" "ecs_cluster_instances" {
  name                 = "${var.app_name}-${var.app_environment}-cluster"
  min_size             = var.size
  max_size             = var.size
  vpc_zone_identifier  = [data.aws_ecs_cluster.example_cluster.*.id]

  tag {
    key                 = "Name"
    value               = "${var.app_name}-${var.app_environment}-cluster"
    propagate_at_launch = true
  }
}