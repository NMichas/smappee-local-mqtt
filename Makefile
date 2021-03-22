BUILD_TAG := $(shell mvn -f ./pom.xml -q -Dexec.executable=echo -Dexec.args='$${project.version}' --non-recursive exec:exec)

all: build-all push-all

build-all: build-src build-amd64 build-arm64 build-armv7

build-src:
	echo "Building image with tag: $(BUILD_TAG)"
	mvn clean install

build-amd64:
	if docker buildx ls | grep -q slm; then echo "slm buildx already exists."; else docker buildx create --name slm --use; fi
	docker --context default buildx build -f Dockerfile.amd64 --platform linux/amd64 . -t nassos/slm:$(BUILD_TAG)-amd64 -t nassos/slm:latest-amd64 --load

build-arm64:
	if docker buildx ls | grep -q slm; then echo "slm buildx already exists."; else docker buildx create --name slm --use; fi
	docker --context default buildx build -f Dockerfile.arm64 --platform linux/arm64 . -t nassos/slm:$(BUILD_TAG)-arm64 -t nassos/slm:latest-arm64 --load

build-armv7:
	if docker buildx ls | grep -q slm; then echo "slm buildx already exists."; else docker buildx create --name slm --use; fi
	docker --context default buildx build -f Dockerfile.armv7 --platform linux/arm/v7 . -t nassos/slm:$(BUILD_TAG)-armv7 -t nassos/slm:latest-armv7 --load

push-all: push-amd64 push-arm64 push-armv7

push-amd64:
	docker --context default push nassos/slm:$(BUILD_TAG)-amd64
	docker --context default push nassos/slm:latest-amd64

push-arm64:
	docker --context default push nassos/slm:$(BUILD_TAG)-arm64
	docker --context default push nassos/slm:latest-arm64

push-armv7:
	docker --context default push nassos/slm:$(BUILD_TAG)-armv7
	docker --context default push nassos/slm:latest-armv7

clear:
	docker buildx rm slm
