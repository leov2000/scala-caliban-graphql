# Scala Caliban Example 

* ### run app from SBT shell
* ### Go to: http://localhost:8088/altair on Chrome or Edge
* ### Enter the URL: http://localhost:8088/api/graphql into the Altair URL bar (not the browser url bar). 
* ### Documentation should load, if not click on the reload icon on the top right.
* ### On the far left dock click on the icon with two arrows. 
  * ### Enter the WS URL: ws://localhost:8088/ws/graphql
  * ### Subscription type: Websocket (graphql-ws)

### Example Mutations 
```graphql
mutation {
  addAccount(name: "john", balance: 32)
}
```

```graphql
mutation {
  debitAccount(name: 0, amount: 35)
}
```

### Example Query
```graphql
query {
  account(account: 0) {
    name
    accountNumber
    balance
    openedOn
  }
}
```
```graphql
query {
  account(name: "john") {
    name
    accountNumber
    balance
    openedOn
  }
}
```
### Example Subscription
```
subscription {
  characterDeleted
}
```