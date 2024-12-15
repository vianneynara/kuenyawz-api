# Controller/Endpoint Directory

## Account

Source: [AccountController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/AccountController.java)

```
GET /api/accounts/{accountID}
PUT /api/accounts/{accountID}
DELETE /api/accounts/{accountID}

GET /api/accounts
POST /api/accounts

PATCH /api/accounts/{accountId}/privilege
PATCH /api/accounts/{accountId}/password
PATCH /api/accounts/{accountId}/account

```

## Simulator

source: [SimulatorController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/SimulatorController.java)
```

GET/api/sim/batch-upload-form

```

## Closure Routes

source: [ClosedDateController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/ClosedDateController.java)
```
GET/api/clousure
POST/api/clousure
DELETE/api/clousure

GET/api/clousure/{closedDateId}
DELETE/api/clousure/{closedDateId}
PATCH/api/clousure/{closedDateId}

GET/api/clousure/available

```

## Order Processing Routes

source: [OrderingController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/OrderingController.java)

```

GET/api/orders
POST/api/orders

POST/api/orders/{purchaseId}/status

GET/api/orders/{purchaseId}/status/next
POST/api/orders/{purchaseId}/status/next

POST/api/orders/{purchaseId}/refaund

POST/api/orders/{purchaseId}/confirm

POST/api/orders/{purchaseId}/cancel

GET/api/orders/{purchaseId}

GET/api/orders/{purchaseId}/transaction

```

## Index Routes

source: [IndexController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/IndexController.java)

```

GET/api/

GET/api/status

GET/api

```

## Authentication Route

source: [AuthController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/AuthController.java)

```

POST/api/auth/revoke

POST/api/auth/register

POST/api/auth/refresh

POST/api/auth/otp/verify

POST/api/auth/otp/request

POST/api/auth/login

POST/api/auth/me

```

## Recommender Controller

source: [RecommenderController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/RecommenderController.java)

```

POST/api/recommender/generate

GET/api.recommender/{productsId}

```

## Midtrans Webhook

source: [MidtransWebhookController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/MidtransWebhookController.java)

```

POST/api/midtrans/sign

POST/api/midtrans/notify

POST/api/midtrans/generate

```

## Product Routes

source: [ProductController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/ProductController.java)

```

GET/api/products
POST/api/products

GET/api/products/{productsId}/variants
POST/api/products/{productsId}/variants

POST/api/products/{productsId}/variants/batch

POST/api/products/import

GET/api/products/{productsId}
DELETE/api/products/{productsId}
PATCH/api/products/{productsId}

DELETE/api/products/{productsId}/variants/{variantId}
PATCH/api/products/{productsId}/variants/{variantId}

PATCH/api/products/{productsId}/availability

GET/api/products/variants

GET/api/products/keyword/{keyword}

GET/api/products/category/{category}

DELETE/api/products/{productId}/permanent

DELETE/api/products/all

DELETE/api/products/all/permanent

```

## Transactions

source: [TransactionController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/TransactionController.java)

```

GET/api/transactions

GET/api/transactions/{transactionId}

```

## Cart Routes

source: [CartController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/CartController.java)

```

GET/api/user/cart

POST/api/user/cart

DELETE/api/user/cart/{cartItemId}
PATCH/api/user/cart/{cartItemId}

DELETE/api/user/cart/all

```

## Product Images Relay Endpoints

source: [ProductImageController.java](https://github.com/vianneynara/kuenyawz-api/blob/feature/233-document-controller-directory-using-readmemd/src/main/java/dev/kons/kuenyawz/controllers/ProductImageController.java)

```

POST/api/image/{productId}
DELETE/api/image/{productId}

POST/api/image/{productId}/batch

GET/api/image/{productId}/{resourceUri}
DELETE/api/image/{productId}/{resourceUri}

DELETE/api/image/all

```