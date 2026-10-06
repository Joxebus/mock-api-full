# mock-api-full

This project use git subtree to include the `mock-api` project as a subdirectory. 

- The `mock-api` project is a Spring Boot application that provides a mock API backend for testing and development purposes.
- The `mock-api-ui` project is a React + Typescript application that provides a UI to manage the mock API.

## Run the project with Docker

Important: Be sure your Docker is running, and you have the latest version of Docker Compose installed. 
For more information, see [DOCKER_CONFIG.md](./DOCKER_CONFIG.md)

```shell
# Build both images and start the stack in the background
docker compose up --build -d
```

### 🚀 Features Preview

<img src="mock-api-frontend/docs/assets/img/01-mock-api-list.png" alt="Dashboard" width="100%">

<details>
    <summary><b>📸 Click to view screenshot gallery</b></summary>
    <br>
    <img src="mock-api-frontend/docs/assets/img/02-mock-api-create-form.png" alt="Create API" width="100%">
    <hr>
    <img src="mock-api-frontend/docs/assets/img/06-mock-api-show-view.png" alt="View API" width="100%">
    <hr>
    <img src="mock-api-frontend/docs/assets/img/08-mock-api-try-out-reponse.png" alt="Try out API" width="100%">
</details>

## Pull changes from the projects

```shell
git subtree pull --prefix=mock-api-backend git@github.com:Joxebus/mock-api.git main --squash
git subtree pull --prefix=mock-api-frontend git@github.com:Joxebus/mock-api-ui.git main --squash
```

## Push changes to the projects

```shell
git subtree push --prefix=mock-api-backend main
git subtree push --prefix=mock-api-frontend main
```

## Docs

- Mock API samples: [docs/mock-api-samples](./docs/mock-api-samples/README.md)
- Backend README: [mock-api-backend](./mock-api-backend/README.md)
- UI README: [mock-api-frontend](./mock-api-frontend/README.md)
- Docker Configuration: [DOCKER_CONFIG.md](./DOCKER_CONFIG.md)