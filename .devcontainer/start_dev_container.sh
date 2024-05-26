docker build . -t mre-dev-container
docker run -it -v $(pwd)/../:/workspaces/Medieval-Roleplay-Engine mre-dev-container /bin/bash
