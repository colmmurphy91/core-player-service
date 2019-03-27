package contracts

import org.springframework.cloud.contract.spec.Contract


Contract.make {

    description("Should Save One Player")

    request{
        method(POST())
        url("/player")
        headers {
            contentType(applicationJsonUtf8())
        }
        body(file("request/postPlayer.json"))
    }

    response{
        status(201)
        headers {
            contentType(applicationJsonUtf8())

        }
        body(file("response/postPlayer.json"))
    }

}