package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("should return one team")


    request {
        method(DELETE())
        url("/player/myID1")
    }

    response {
        status(204)
    }
}
