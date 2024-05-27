docker build . -t mre-dev-container
docker run -it -v %cd%\..\:/workspaces/Medieval-Roleplay-Engine mre-dev-container /bin/bash
