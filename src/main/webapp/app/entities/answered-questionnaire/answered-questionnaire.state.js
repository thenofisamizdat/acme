(function() {
    'use strict';

    angular
        .module('acmeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('answered-questionnaire', {
            parent: 'entity',
            url: '/answered-questionnaire',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AnsweredQuestionnaires'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/answered-questionnaire/answered-questionnaires.html',
                    controller: 'AnsweredQuestionnaireController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('answered-questionnaire-detail', {
            parent: 'answered-questionnaire',
            url: '/answered-questionnaire/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AnsweredQuestionnaire'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/answered-questionnaire/answered-questionnaire-detail.html',
                    controller: 'AnsweredQuestionnaireDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'AnsweredQuestionnaire', function($stateParams, AnsweredQuestionnaire) {
                    return AnsweredQuestionnaire.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'answered-questionnaire',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('answered-questionnaire-detail.edit', {
            parent: 'answered-questionnaire-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answered-questionnaire/answered-questionnaire-dialog.html',
                    controller: 'AnsweredQuestionnaireDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AnsweredQuestionnaire', function(AnsweredQuestionnaire) {
                            return AnsweredQuestionnaire.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('answered-questionnaire.new', {
            parent: 'answered-questionnaire',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answered-questionnaire/answered-questionnaire-dialog.html',
                    controller: 'AnsweredQuestionnaireDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                questionnaireID: null,
                                createdBy: null,
                                answeredBy: null,
                                answeredDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('answered-questionnaire', null, { reload: 'answered-questionnaire' });
                }, function() {
                    $state.go('answered-questionnaire');
                });
            }]
        })
        .state('answered-questionnaire.edit', {
            parent: 'answered-questionnaire',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answered-questionnaire/answered-questionnaire-dialog.html',
                    controller: 'AnsweredQuestionnaireDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AnsweredQuestionnaire', function(AnsweredQuestionnaire) {
                            return AnsweredQuestionnaire.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('answered-questionnaire', null, { reload: 'answered-questionnaire' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('answered-questionnaire.delete', {
            parent: 'answered-questionnaire',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answered-questionnaire/answered-questionnaire-delete-dialog.html',
                    controller: 'AnsweredQuestionnaireDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AnsweredQuestionnaire', function(AnsweredQuestionnaire) {
                            return AnsweredQuestionnaire.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('answered-questionnaire', null, { reload: 'answered-questionnaire' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
