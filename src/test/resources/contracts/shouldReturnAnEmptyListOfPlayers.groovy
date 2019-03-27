package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description("should return one team")


    request {
        method(GET())
        url("/players?teamId=teamWithNoPlayers")
    }

    response {
        status(200)
        headers {
            contentType(applicationJsonUtf8())
        }
        body("""
        []
        """)
    }
}