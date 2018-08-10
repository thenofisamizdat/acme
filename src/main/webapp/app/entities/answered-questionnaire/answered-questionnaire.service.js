(function() {
    'use strict';
    angular
        .module('acmeApp')
        .factory('AnsweredQuestionnaire', AnsweredQuestionnaire);

    AnsweredQuestionnaire.$inject = ['$resource', 'DateUtils'];

    function AnsweredQuestionnaire ($resource, DateUtils) {
        var resourceUrl =  'api/answered-questionnaires/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.answeredDate = DateUtils.convertDateTimeFromServer(data.answeredDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
