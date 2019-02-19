/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ofstedformsfrontend.connectors

import java.util.UUID

import javax.inject.{Inject, Named, Singleton}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ofstedformsfrontend.forms.{FormId, Occurrence}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class NotificationId(value: UUID)

object NotificationId {
  def apply(value: String): Try[NotificationId] = Try {
    new NotificationId(UUID.fromString(value))
  }
}

@Singleton
class NotificationsConnector @Inject()(httpClient: HttpClient,
                                      @Named("ofsted-forms-notifications-base-url") baseUrl: String)
                                      (implicit ec: ExecutionContext){

  private val submissionUrl = baseUrl + "/ofsted-forms-notifications/submission"

  def submission(formId: FormId, email: String, submission: Occurrence)(implicit hc: HeaderCarrier): Future[NotificationId] = {
    val payload = Json.obj(
      "time" -> submission.moment,
      "email" -> email,
      "id" -> formId.value.toString
    )
    httpClient.POST(submissionUrl, payload, Seq.empty).flatMap(request =>
      Future.fromTry(NotificationId(request.body))
    )
  }

  private val acceptanceUrl = baseUrl + "/ofsted-forms-notifications/acceptance"

  def acceptance(formId: FormId, email: String, submission: Occurrence)(implicit hc: HeaderCarrier): Future[NotificationId] = {
    val payload = Json.obj(
      "time" -> submission.moment,
      "email" -> email,
      "id" -> formId.value.toString
    )
    httpClient.POST(acceptanceUrl, payload, Seq.empty).flatMap(request =>
      Future.fromTry(NotificationId(request.body))
    )
  }

  private val rejectionUrl = baseUrl + "/ofsted-forms-notifications/rejection"

  def rejection(formId: FormId, email: String, submission: Occurrence)(implicit hc: HeaderCarrier): Future[NotificationId] = {
    val payload = Json.obj(
      "time" -> submission.moment,
      "email" -> email,
      "id" -> formId.value.toString
    )
    httpClient.POST(rejectionUrl, payload, Seq.empty).flatMap(request =>
      Future.fromTry(NotificationId(request.body))
    )
  }


}
