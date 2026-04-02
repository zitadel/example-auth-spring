.PHONY: help check prepare start

help:
	@echo "Usage:"
	@echo "  make start     Start the development server"
	@echo "  make prepare   Build and install dependencies"
	@echo "  make check     Verify required dependencies are installed"

check:
	@command -v java >/dev/null 2>&1 || { \
		echo "Error: Java is not installed." >&2; \
		echo "" >&2; \
		echo "  Install a JDK from https://adoptium.net/ or use sdkman:" >&2; \
		echo "    curl -s https://get.sdkman.io | bash" >&2; \
		echo "    sdk install java" >&2; \
		exit 1; \
	}
	@command -v mvn >/dev/null 2>&1 || { \
		echo "Error: Maven is not installed." >&2; \
		echo "" >&2; \
		echo "  Install it from https://maven.apache.org/download.cgi or use sdkman:" >&2; \
		echo "    sdk install maven" >&2; \
		exit 1; \
	}
	@test -f .env || { \
		echo "Error: Missing .env file." >&2; \
		echo "" >&2; \
		echo "  Copy the example file and fill in your Zitadel credentials:" >&2; \
		echo "    cp .env.example .env" >&2; \
		exit 1; \
	}

prepare: check
	mvn clean install

start: check
	mvn spring-boot:run
