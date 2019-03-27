package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description("should return one team")


    request {
        method(GET())
        url("/players?teamId=id")
    }

    response {
        status(200)
        headers {
            contentType(applicationJsonUtf8())
        }
        body("""
        [{
            "id" : "myID1", 
            "firstName" : "Colm",
            "lastName" : "Murphy",
            "email" : "colm.j.murphy91@gmail.com",
            "teamId": "teamId"
        }]
        """)
    }
}