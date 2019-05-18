pipeline {
  agent {
    label "jenkins-maven"
  }
  environment {
    ORG = 'colmmurphy91'
    APP_NAME = 'core-player-service'
    CHARTMUSEUM_CREDS = credentials('jenkins-x-chartmuseum')
  }
  stages {
    stage('CI Build and push snapshot') {
      when {
        branch 'PR-*'
      }
      environment {
        PREVIEW_VERSION = "0.0.0-SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER"
        PREVIEW_NAMESPACE = "$APP_NAME-$BRANCH_NAME".toLowerCase()
        HELM_RELEASE = "$PREVIEW_NAMESPACE".toLowerCase()
      }
      steps {
        container('maven') {
          sh "mvn versions:set -DnewVersion=$PREVIEW_VERSION"
          sh "mvn install"
          sh "skaffold version"
          sh "export VERSION=$PREVIEW_VERSION && skaffold build -f skaffold.yaml"
          sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:$PREVIEW_VERSION"
          dir('charts/preview') {
            sh "make preview"
            sh "jx preview --app $APP_NAME --dir ../.."
          }
        }
      }
    }
        stage('Build Staging') {
          when {
            branch 'staging'
          }
          steps {
            container('maven') {

              // ensure we're not on a detached head
              sh "git checkout staging"
              sh "git config --global credential.helper store"
              sh "jx step git credentials"

              // so we can retrieve the version in later steps
              sh "echo \$(jx-release-version) > VERSION"
              sh "mvn versions:set -DnewVersion=\$(cat VERSION)"
              sh "jx step tag --version \$(cat VERSION)"
              sh "mvn clean deploy"
              sh "skaffold version"
              sh "export VERSION=`cat VERSION` && skaffold build -f skaffold.yaml "
              sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:\$(cat VERSION)"
            }
          }
        }
    stage('Build Release') {
      when {
        branch 'master'
      }
      steps {
        container('maven') {

          // ensure we're not on a detached head
          sh "git checkout master"
          sh "git config --global credential.helper store"
          sh "jx step git credentials"

          // so we can retrieve the version in later steps
          sh "echo \$(jx-release-version) > VERSION"
          sh "mvn versions:set -DnewVersion=\$(cat VERSION)"
          sh "jx step tag --version \$(cat VERSION)"
          sh "mvn clean deploy"
          sh "skaffold version"
          sh "export VERSION=`cat VERSION` && skaffold build -f skaffold.yaml "
          sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:\$(cat VERSION)"
        }
      }
    }
    stage('Promote to Production') {
      when {
        branch 'master'
      }
      steps {
        container('maven') {
          dir('charts/core-player-service-production') {
            sh "jx step changelog --version v\$(cat ../../VERSION)"

            // release the helm chart
            sh "jx step helm release"

            sh "jx promote -b --env production --timeout 1h --version \$(cat ../../VERSION)"
          }
        }
      }
    }
     stage('Promote to Staging') {
       when {
            branch 'staging'
          }
          steps {
            container('maven') {
              dir('charts/core-player-service') {
                sh "jx step changelog --version v\$(cat ../../VERSION)"

                // release the helm chart
                sh "jx step helm release"

                sh "jx promote -b --env staging --timeout 1h --version \$(cat ../../VERSION)"
              }
            }
          }
        }
  }
  post {
        always {
          cleanWs()
        }
  }
}
